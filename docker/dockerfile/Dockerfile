FROM ubuntu
RUN mkdir /root/packages
COPY ./packages/* /root/packages/
COPY ./install-package.sh /root/
RUN /bin/bash /root/install-package.sh
