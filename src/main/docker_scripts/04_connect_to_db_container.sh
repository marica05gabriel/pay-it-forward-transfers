#!/bin/bash
docker exec -it transfer_db psql -U db_user -d db_transfer
