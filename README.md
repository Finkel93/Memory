# Memory

[![Coverage Status](https://coveralls.io/repos/github/Finkel93/Memory/badge.svg?branch=CI)](https://coveralls.io/github/Finkel93/Memory?branch=CI)

Running the container:

1. `sudo systemctl start docker`
2. `sudo systemctl status docker`
3. `docker build -t memory-app .`
4. `xhost +local:docker`
5. `sudo docker run -it --rm -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix memory-app`
