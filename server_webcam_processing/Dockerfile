FROM python:3-slim

WORKDIR /usr/app/src

RUN apt-get update \
    && apt-get install -y libgl1-mesa-glx libglib2.0-0
COPY requirements.txt /usr/app/src/
COPY . /usr/app/src/

RUN pip install -r requirements.txt

CMD [ "python3", "sample.py"]