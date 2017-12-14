FROM openjdk:8
MAINTAINER ome-devel@lists.openmicroscopy.org.uk

# Installs Ant
ENV ANT_VERSION 1.9.4
RUN wget -q http://archive.apache.org/dist/ant/binaries/apache-ant-${ANT_VERSION}-bin.zip && \
  unzip apache-ant-${ANT_VERSION}-bin.zip && \
  mv apache-ant-${ANT_VERSION} /opt/ant && \
  rm apache-ant-${ANT_VERSION}-bin.zip

RUN useradd -m bf
COPY . /opt/bioformats/
RUN chown -R bf /opt/bioformats

USER bf
WORKDIR /opt/bioformats
RUN /opt/ant/bin/ant clean jars tools

WORKDIR /opt/bioformats/components/test-suite
ENTRYPOINT ["/opt/ant/bin/ant", "test-automated", "-Dtestng.directory=/opt/data", "-Dtestng.configDirectory=/opt/config"]
