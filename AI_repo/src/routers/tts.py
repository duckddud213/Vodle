from fastapi import APIRouter, HTTPException
from fastapi.responses import FileResponse
import os
from ..utils import generate_unique_filename
from rvc_infer import rvc_convert
import pyttsx3
from ..schemas.tts import TTSRequest
import time
import logging
import asyncio

from concurrent.futures import ThreadPoolExecutor
from functools import partial

async def async_rvc_convert(selected_voice: str, input_file_path: str, file_name: str):
    rvc_convert_partial = partial(rvc_convert, model_path=f"src/models/{selected_voice}.pth", input_path=input_file_path, output_file_name=file_name)

    loop = asyncio.get_running_loop()
    with ThreadPoolExecutor(max_workers=5) as pool:
        await loop.run_in_executor(pool, rvc_convert_partial)

router = APIRouter(prefix="/tts", tags=["TTS"])

logger = logging.getLogger(__name__)

@router.post("")
async def create_tts_file(request: TTSRequest) -> FileResponse:
    engine = pyttsx3.init()
    try:
        start_time = time.time() # 메소드 실행 !

        # engine.setProperty('rate', 150)  # 말하기 속도 
        voices = engine.getProperty('voices')
        engine.setProperty('voice', voices[0].id)
        engine.setProperty('volume', 1)

        print("파일 이름 만들기 시작: ",time.time()-start_time)
        file_name = generate_unique_filename("tts")
        print("파일 이름 만들기 끝: ",time.time()-start_time)
        input_file_path = f"input/{file_name}.wav"

        print("tts input 파일 저장 시작: ",time.time()-start_time)
        engine.save_to_file(request.content, input_file_path)
        print("tts input 파일 저장 끝",time.time()-start_time) 
        engine.runAndWait()

        output_file_name = f"{file_name}.wav"

        print("변환 시작: ",time.time()-start_time)
        await async_rvc_convert(request.selected_voice, input_file_path, output_file_name)
        print("변환 끝: ",time.time()-start_time)
        
        response = FileResponse(f"output/{output_file_name}", filename=output_file_name)
        
        end_time = time.time() 
        logger.info(f"voice_conversion 실행 시간: {end_time - start_time}초")  #

        return response
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
