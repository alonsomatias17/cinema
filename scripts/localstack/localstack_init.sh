#!/usr/bin/env sh

aws dynamodb --endpoint-url=http://localhost:4566  delete-table \
    --table-name Movies

aws dynamodb --endpoint-url=http://localhost:4566 create-table \
    --table-name Movies \
    --attribute-definitions \
        AttributeName=PK,AttributeType=S \
        AttributeName=SK,AttributeType=S \
    --key-schema \
        AttributeName=PK,KeyType=HASH \
        AttributeName=SK,KeyType=RANGE \
    --provisioned-throughput \
        ReadCapacityUnits=10,WriteCapacityUnits=5

aws --endpoint-url=http://localhost:4566 dynamodb put-item \
    --table-name Movies  \
    --item \
        '{"PK": {"S": "mo#12345"}, "SK": {"S": "mo#12345"}, "title": {"S": "The Fast and the Furious"}, "imdbId": {"S": "tt0232500"}, "ticketPrice": {"N": "10"}, "schedules": {"L": [{"M":{"times":{"L":[{"S":"00:30"},{"S":"10:45"}]},"dayOfWeek":{"S":"MONDAY"}}}]}}'