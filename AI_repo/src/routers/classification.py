from fastapi import APIRouter
import whisper
import os
from transformers import pipeline

router = APIRouter(prefix="/classification", tags=["Classification"])

@router.post("")
async def text_classification(text: str) -> str:
    try:
        zeroshot_classifier = pipeline("zero-shot-classification", model="MoritzLaurer/deberta-v3-large-zeroshot-v1.1-all-33")

        ## finetunning을 위한 template
        hypothesis_template = "This record is about {}"

        ## 분류할 카테고리 리스트
        classes_verbalized = ["RESTAURANT", "SMALLTALK", "TRAVEL"]
        
        output = zeroshot_classifier(text, classes_verbalized, hypothesis_template=hypothesis_template, multi_label=False)

        result = output['labels'][0]
        return result
    except Exception as e:
        return str(e)
