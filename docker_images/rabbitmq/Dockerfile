FROM alpine:3.5

RUN apk add --update curl && rm -rf /var/cache/apk/*

ENV             VERSION v3.1.7

WORKDIR         /opt
RUN             curl -L https://github.com/coreos/etcd/releases/download/$VERSION/etcd-$VERSION-linux-amd64.tar.gz \
                    -o etcd-$VERSION-linux-amd64.tar.gz
RUN             tar xzvf etcd-$VERSION-linux-amd64.tar.gz
RUN             ln -s /opt/etcd-$VERSION-linux-amd64/etcd /usr/local/bin/etcd &&\
                ln -s /opt/etcd-$VERSION-linux-amd64/etcdctl /usr/local/bin/etcdctl

# Configure Spoke behavior
ENV             SPOKE_NAME etcd
ENV             SPOKE_CMD etcdctl

CMD		etcd
