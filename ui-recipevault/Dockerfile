# Install the application dependencies in a full UBI Node docker image
FROM registry.access.redhat.com/ubi8/nodejs-14:latest as builder

WORKDIR /tmp/src
USER 0
ADD . /tmp/src
RUN chown -R 1001:0 /tmp/src
USER 1001

# Install the dependencies
RUN npm install && \
   npm i -g corepack

RUN yarn build


# Copy the dependencies into a minimal Node.js image
FROM registry.redhat.io/rhel8/nginx-118:1-48

USER 0
COPY --from=builder /tmp/src/dist /tmp/src/
RUN chown -R 1001:0 /tmp/src

COPY nginx/nginx.conf "${NGINX_CONF_PATH}"


USER 1001

# Let the assemble script to install the dependencies
RUN /usr/libexec/s2i/assemble

COPY ./s2i/bin /usr/libexec/s2i

# Run script uses standard ways to run the application
CMD /usr/libexec/s2i/run