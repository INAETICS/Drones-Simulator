FROM rabbitmq:management-alpine

RUN apk add --update curl && \
    rm -rf /var/cache/apk/*

ADD register_rabbitmq.sh /opt/register_rabbitmq.sh
RUN chmod +x /opt/register_rabbitmq.sh

#ENTRYPOINT /opt/register_rabbitmq.sh
CMD ["/opt/register_rabbitmq.sh"]
