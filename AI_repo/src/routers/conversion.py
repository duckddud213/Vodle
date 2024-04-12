from fastapi import APIRouter, HTTPException, UploadFile, File
from fastapi.responses import FileResponse
from ..utils import generate_unique_filename
from rvc_infer import rvc_convert
import time
import logging
import aiofiles
import asyncio


from concurrent.futures import ThreadPoolExecutor
from functools import partial

async def async_rvc_convert(selected_voice: str, input_file_path: str, file_name: str, pitch_change: int):
    rvc_convert_partial = partial(rvc_convert, model_path=f"src/models/{selected_voice}.pth", input_path=input_file_path, output_file_name=file_name, f0_up_key=pitch_change)

    loop = asyncio.get_running_loop()
    with ThreadPoolExecutor(max_workers=5) as pool:
        await loop.run_in_executor(pool, rvc_convert_partial)

router = APIRouter(prefix="/conversion", tags=["Conversion"])

logger = logging.getLogger(__name__)

@router.post("/{selected_voice}/{pitch_change}")
async def voice_conversion(selected_voice: str, pitch_change:int, audio_file: UploadFile = File(...)) -> File:
    try:
        start_time = time.time() # 메소드 실행 !
        if pitch_change < -24 or pitch_change > 24:
            raise HTTPException(status_code=400, detail="2옥타브 (-24 ~ 24) 범위 내에서 입력해 주세요.")     

        print("파일 이름 만들기 시작: ",time.time()-start_time)
        file_name = generate_unique_filename(audio_file.filename)
        print("파일 이름 만들기 끝: ",time.time()-start_time)

        # 파일을 디스크에 저장
        input_file_path = f"input/{file_name}"
        async with aiofiles.open(input_file_path, "wb") as file_object:
            await file_object.write(await audio_file.read())
            
        print("input 파일 저장 끝: ",time.time()-start_time)
        print("변환 시작: ",time.time()-start_time)

        await async_rvc_convert(selected_voice, input_file_path, file_name, pitch_change)

        print("변환 끝: ",time.time()-start_time)

        response = FileResponse(f"output/{file_name}", filename=file_name)

        end_time = time.time() 
        logger.info(f"voice_conversion 실행 시간: {end_time - start_time}초") 

        return response
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
