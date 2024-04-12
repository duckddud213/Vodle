FROM python:3.9.5

WORKDIR /code

COPY . /code

RUN pip install -r /code/requirements.txt

RUN apt-get update && \
    apt-get install -y ffmpeg libespeak1

CMD ["uvicorn", "src.main:app", "--reload", "--host", "0.0.0.0", "--port", "8082"]
