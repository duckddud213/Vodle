from fastapi import FastAPI
from .routers import stt, tts, conversion, classification

app = FastAPI()

routers = [stt.router, tts.router ,conversion.router, classification.router]

for router in routers:
    app.include_router(router, prefix="/api/ai")


from fastapi.middleware.cors import CORSMiddleware

origins = [
    "*",
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)