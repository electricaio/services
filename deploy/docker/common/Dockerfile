FROM sgrio/java-oracle:server_jre_8

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS

# Create user
RUN adduser --disabled-password --gecos '' --shell /bin/sh electrica

# Create and grant log directory
RUN mkdir -p /var/log/electrica && chown electrica:electrica -R /var/log/electrica

# Set workdir
WORKDIR /home/electrica

# Copy and grant entrypoint
ADD entrypoint.sh entrypoint.sh
RUN chmod 755 entrypoint.sh && chown electrica:electrica entrypoint.sh

# Change user
USER electrica

# Create configuration folder
RUN mkdir -p config

ENTRYPOINT ["./entrypoint.sh"]
