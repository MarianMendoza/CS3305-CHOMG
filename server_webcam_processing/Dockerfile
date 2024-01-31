FROM python:3-slim

WORKDIR /usr/app/src

COPY requirements.txt /usr/app/src/
COPY . /usr/app/src/app

RUN pip install -r requirements.txt

CMD [ "python3", "sample.py"]