/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.gearpump.streaming

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import org.apache.gearpump._
import org.apache.gearpump.cluster.MasterToAppMaster.ReplayFromTimestampWindowTrailingEdge
import org.apache.gearpump.cluster.scheduler.Resource
import org.apache.gearpump.cluster.{ExecutorContextInterface, ApplicationExecutor, UserConfig}
import org.apache.gearpump.streaming.AppMasterToExecutor._
import org.apache.gearpump.streaming.ExecutorToAppMaster.RegisterExecutor
import org.apache.gearpump.streaming.task.{TaskId, TaskLocations}
import org.apache.gearpump.transport.{Express, HostPort}
import org.apache.gearpump.util.{Constants, LogUtil}
import org.slf4j.Logger

import scala.concurrent.duration._

case object TaskLocationReady

class Executor(executorContext: ExecutorContextInterface, userConf : UserConfig)  extends ApplicationExecutor {

  import context.dispatcher
  import executorContext._

  private val LOG: Logger = LogUtil.getLogger(getClass, executor = executorId,
    app = appId)

  LOG.info(s"Executor ${executorId} has been started, start to register itself...")

  appMaster ! RegisterExecutor(self, executorId, resource, workerId)
  context.watch(appMaster)

  val express = Express(context.system)

  def receive : Receive = appMasterMsgHandler orElse terminationWatch

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: MsgLostException =>
        LOG.info("We got MessageLossException from task, replaying application...")
        appMaster ! ReplayFromTimestampWindowTrailingEdge
        Restart
      case _: RestartException => Restart
    }

  def appMasterMsgHandler : Receive = {
    case LaunchTask(taskId, taskContext, taskClass) => {
      LOG.info(s"Launching Task $taskId for app: ${appId}, $taskClass")
      val taskDispatcher = context.system.settings.config.getString(Constants.GEARPUMP_TASK_DISPATCHER)
      val task = context.actorOf(Props(taskClass, taskContext, userConf).withDispatcher(taskDispatcher), "group_" + taskId.groupId + "_task_" + taskId.index)
    }
    case TaskLocations(locations) =>
      val result = locations.flatMap { kv =>
        val (host, taskIdList) = kv
        taskIdList.map(taskId => (TaskId.toLong(taskId), host))
      }
      express.startClients(locations.keySet).map { _ =>
        express.remoteAddressMap.send(result)
        express.remoteAddressMap.future().map(result => context.children.foreach(_ ! TaskLocationReady))
      }
    case r @ RestartTasks(clock) =>
      LOG.info(s"Executor received restart tasks at time: $clock")
      express.remoteAddressMap.send(Map.empty[Long, HostPort])
      context.children.foreach(_ ! r)
  }

  def terminationWatch : Receive = {
    case Terminated(actor) => {
      if (actor.compareTo(appMaster) == 0) {

        LOG.info(s"AppMaster ${appMaster.path.toString} is terminated, shutting down current executor $appId, $executorId")

        context.stop(self)
      }
    }
  }
}