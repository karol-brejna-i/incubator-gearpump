# Docker base file for Gearpump

FROM grubykarol/jre

ENV GP_VERSION 0.8.2
ENV GP_ROOT /gearpump
ENV GP_NAME gearpump_2.11-$GP_VERSION
ENV GP_HOME ${GP_ROOT}/${GP_NAME}

RUN echo $GP_VERSION $GP_ROOT $GP_NAME $GP_HOME


# prepare the package
# https://dist.apache.org/repos/dist/release/incubator/gearpump/0.8.2-incubating/gearpump_2.11-0.8.2-incubating-bin.tgz

RUN echo "https://dist.apache.org/repos/dist/release/incubator/gearpump/${GP_VERSION}-incubating/${GP_NAME}-incubating-bin.tgz"


RUN curl --location --retry 3 --insecure \
   https://dist.apache.org/repos/dist/release/incubator/gearpump/${GP_VERSION}-incubating/${GP_NAME}-incubating-bin.tgz -o tmp.tgz 

#RUN tar -xvzf tmp.tgz

#RUN rm tmp.tgz && \
#    chmod +x ${GP_HOME}/bin/*

# copy gearpump config
#ADD conf/gear.conf ${GP_HOME}/conf/gear.conf



# this file needs to be at the project root for this to work
#ADD . ${GP_ROOT}
#WORKDIR ${GP_ROOT}

ENTRYPOINT [ "/bin/sh" ]
