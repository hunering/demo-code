#!/usr/bin/env bash
#1. put this file to /home/allen/setup/kubernetes/cluster/ubuntu
#2. navigate to /home/allen/setup/kubernetes/cluster
#3. run ./ubuntu/deploy-node.sh 192.168.1.106
# you may fail to start docker after deployment, check file /etc/default/docker

function provision-node() {

  echo -e "\nDeploying node on machine ${1#*@}"

  ssh $SSH_OPTS $1 "mkdir -p ~/kube/default"

  # copy the binaries and scripts to the ~/kube directory on the node
  scp -r $SSH_OPTS \
    "${KUBE_CONFIG_FILE}" \
    ubuntu/util.sh \
    ubuntu/reconfDocker.sh \
    ubuntu/minion/* \
    ubuntu/binaries/minion \
    "${1}:~/kube"

  if [ -z "$CNI_PLUGIN_CONF" ] || [ -z "$CNI_PLUGIN_EXES" ]; then
    # Prep for Flannel use: copy the flannel binaries and scripts, set reconf flag
    scp -r $SSH_OPTS ubuntu/minion-flannel/* "${1}:~/kube"
    SERVICE_STARTS="service flanneld start"
    NEED_RECONFIG_DOCKER=true
    CNI_PLUGIN_CONF=''

  else
    # Prep for CNI use: copy the CNI config and binaries, adjust upstart config, set reconf flag
    ssh $SSH_OPTS "${1}" "rm -rf tmp-cni; mkdir -p tmp-cni/exes tmp-cni/conf"
    scp    $SSH_OPTS "$CNI_PLUGIN_CONF" "${1}:tmp-cni/conf/"
    scp -p $SSH_OPTS  $CNI_PLUGIN_EXES  "${1}:tmp-cni/exes/"
    ssh $SSH_OPTS -t "${1}" '
      sudo -p "[sudo] password to prep node %h: " -- /bin/bash -ce "
        mkdir -p /opt/cni/bin /etc/cni/net.d
        cp ~$(id -un)/tmp-cni/conf/* /etc/cni/net.d/
        cp --preserve=mode ~$(id -un)/tmp-cni/exes/* /opt/cni/bin/
        '"sed -i.bak -e 's/start on started flanneld/start on started ${CNI_KUBELET_TRIGGER}/' -e 's/stop on stopping flanneld/stop on stopping ${CNI_KUBELET_TRIGGER}/' "'~$(id -un)/kube/init_conf/kubelet.conf
        '"sed -i.bak -e 's/start on started flanneld/start on started networking/' -e 's/stop on stopping flanneld/stop on stopping networking/' "'~$(id -un)/kube/init_conf/kube-proxy.conf
        "'
    SERVICE_STARTS='service kubelet    start
                    service kube-proxy start'
    NEED_RECONFIG_DOCKER=false
  fi

  BASH_DEBUG_FLAGS=""
  if [[ "$DEBUG" == "true" ]] ; then
    BASH_DEBUG_FLAGS="set -x"
  fi

  # remote login to node and configue k8s node
  ssh $SSH_OPTS -t "$1" "
    set +e
    ${BASH_DEBUG_FLAGS}
    source ~/kube/util.sh

    setClusterInfo
    create-kubelet-opts \
      '${1#*@}' \
      '${MASTER_IP}' \
      '${DNS_SERVER_IP}' \
      '${DNS_DOMAIN}' \
      '${KUBELET_CONFIG}' \
      '${ALLOW_PRIVILEGED}' \
      '${CNI_PLUGIN_CONF}'
    create-kube-proxy-opts \
      '${1#*@}' \
      '${MASTER_IP}' \
      '${KUBE_PROXY_EXTRA_OPTS}'
    create-flanneld-opts '${MASTER_IP}' '${1#*@}'

    sudo -E -p '[sudo] password to start node: ' -- /bin/bash -ce '
      ${BASH_DEBUG_FLAGS}
      cp ~/kube/default/* /etc/default/
      cp ~/kube/init_conf/* /etc/init/
      cp ~/kube/init_scripts/* /etc/init.d/
      mkdir -p /opt/bin/
      cp ~/kube/minion/* /opt/bin
      ${SERVICE_STARTS}
      if ${NEED_RECONFIG_DOCKER}; then KUBE_CONFIG_FILE=\"${KUBE_CONFIG_FILE}\" DOCKER_OPTS=\"${DOCKER_OPTS}\" ~/kube/reconfDocker.sh i; fi
      '" || {
      echo "Deploying node on machine ${1#*@} failed"
      exit 1
  }
}



MASTER_IP=192.168.1.133
KUBE_ROOT=/home/allen/setup/kubernetes
SSH_OPTS="-oStrictHostKeyChecking=no -oUserKnownHostsFile=/dev/null -oLogLevel=ERROR"

export KUBE_CONFIG_FILE=${KUBE_ROOT}/cluster/ubuntu/config-default.sh
echo "${KUBE_CONFIG_FILE}"
source "${KUBE_CONFIG_FILE}"
provision-node $1

