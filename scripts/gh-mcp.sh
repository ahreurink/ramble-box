#!/bin/bash
docker run -i --rm --name github-mcp \
  -e GITHUB_PERSONAL_ACCESS_TOKEN=$GITHUB_TOKEN \
  -e GITHUB_TOOLSETS="issues" \
  -v /var/log:/var/log \
  ghcr.io/github/github-mcp-server stdio --log-file /var/log/github-mcp-server.log
