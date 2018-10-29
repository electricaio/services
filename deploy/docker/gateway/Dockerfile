FROM nginx:1.15-alpine

RUN mkdir -p /etc/nginx/conf.d

COPY nginx/nginx.conf /etc/nginx/
COPY nginx/cors.conf /etc/nginx/

COPY nginx/conf.d/api.conf.template /etc/nginx/conf.d
COPY nginx/conf.d/upstreams.conf /etc/nginx/conf.d

# Copy and grant entrypoint
ADD entrypoint.sh entrypoint.sh
RUN chmod 755 entrypoint.sh && chown nginx:nginx entrypoint.sh

ENTRYPOINT ["./entrypoint.sh"]