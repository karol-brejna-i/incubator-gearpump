# Docker base file for GearPump

FROM errordeveloper/oracle-jre

ENV GP_VERSION=0.8.0 \
    GP_ROOT="/gearpump"
ENV GP_NAME="gearpump-2.11-${GP_VERSION}"
ENV GP_HOME="${GP_ROOT}/${GP_NAME}"

# this file needs to be at the project root for this to work
ADD . ${GP_ROOT}
WORKDIR ${GP_ROOT}

# prepare the package


RUN curl --location --retry 3 --insecure https://github.com/gearpump/gearpump/releases/download/${GP_VERSION}/${GP_NAME}.zip -o tmp.zip && \
    unzip -q tmp.zip && \
    rm tmp.zip && \
    chmod +x ${GP_HOME}/bin/*

# copy gearpump config
ADD conf/gear.conf ${GP_HOME}/conf/gear.conf
