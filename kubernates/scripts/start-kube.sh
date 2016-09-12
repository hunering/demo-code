case "$1" in
    start)
        service etcd start
        service flanneld start
        service kube-apiserver start
        service kube-controller-manager start
        service kubelet start
        service kube-proxy start
        service kube-scheduler start
        ;;

    stop)
        service etcd stop
                service flanneld stop
                service kube-apiserver stop
                service kube-controller-manager stop
                service kubelet stop
                service kube-proxy stop
                service kube-scheduler stop
        ;;

    restart | force-reload)
        ;;

    status)
        service etcd status
                service flanneld status
                service kube-apiserver status
                service kube-controller-manager status
                service kubelet status
                service kube-proxy status
                service kube-scheduler status
        ;;

    *)
        echo "Usage: $0 {start|stop|restart|status}"
        exit 1
        ;;
esac

