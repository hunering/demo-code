#/bin/sh
#https_proxy=http://huxiaomi:****@child-prc.intel.com:914
echo "https_proxy is:${https_proxy}"
helm install stable/nginx-ingress --name ingress-nginx --set rbac.create=true --set controller.service.externalIPs[0]=192.168.1.134 $@
