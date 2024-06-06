#!/bin/bash
docker run --name transfer_db \
          -e POSTGRES_PASSWORD=password \
          -p 8071:5432 \
          -v db_volume_transfer:/var/lib/postgresql/data \
          -d transfer_db