import json
import os
from typing import List, Optional
from fastapi import FastAPI, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

app = FastAPI(
    title="AfterScene Mood Recommendations API",
    description="API para recomendações de obras (Animes, Séries, Doramas, Filmes) baseadas em humor/mood.",
    version="1.0.0"
)

# Permitir CORS para testes locais e emuladores
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

MOODS_FILE_PATH = os.path.join(os.path.dirname(__file__), "moods.json")


# Modelos Pydantic
class WorkCreate(BaseModel):
    title: str = Field(..., min_length=1, description="Título da obra")
    type: str = Field(..., description="Tipo da obra (Anime, Dorama, Série, Filme)")
    description: str = Field(..., description="Descrição ou motivo da recomendação")


class Work(BaseModel):
    id: int
    title: str
    type: str
    description: str


class MoodCategory(BaseModel):
    id: int
    name: str
    emoji: Optional[str] = "✨"
    description: Optional[str] = ""
    works: List[Work] = []


class MoodsDatabase(BaseModel):
    moods: List[MoodCategory]


def load_database() -> MoodsDatabase:
    if not os.path.exists(MOODS_FILE_PATH):
        return MoodsDatabase(moods=[])
    try:
        with open(MOODS_FILE_PATH, "r", encoding="utf-8") as f:
            data = json.load(f)
            return MoodsDatabase(**data)
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erro ao carregar banco de dados de moods: {str(e)}"
        )


def save_database(data: MoodsDatabase):
    try:
        with open(MOODS_FILE_PATH, "w", encoding="utf-8") as f:
            json.dump(data.model_dump(), f, ensure_ascii=False, indent=2)
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erro ao salvar banco de dados de moods: {str(e)}"
        )


@app.get("/", tags=["Status"])
def root():
    return {
        "app": "AfterScene API",
        "status": "online",
        "endpoints": [
            "GET /moods - Lista todas as categorias de humor e suas obras",
            "GET /moods/{mood_id}/works - Lista obras recomendadas para um mood específico",
            "POST /moods/{mood_id}/works - Sugere uma nova obra para um mood"
        ]
    }


@app.get("/moods", response_model=List[MoodCategory], tags=["Moods"])
def get_moods():
    """Retorna todas as categorias de humor e suas obras cadastradas."""
    db = load_database()
    return db.moods


@app.get("/moods/{mood_id}", response_model=MoodCategory, tags=["Moods"])
def get_mood_by_id(mood_id: int):
    """Retorna os dados de uma categoria de mood específica."""
    db = load_database()
    for mood in db.moods:
        if mood.id == mood_id:
            return mood
    raise HTTPException(status_code=404, detail=f"Categoria de mood com ID {mood_id} não encontrada")


@app.get("/moods/{mood_id}/works", response_model=List[Work], tags=["Moods"])
def get_works_by_mood(mood_id: int):
    """Retorna as obras recomendadas para uma categoria de humor específica."""
    db = load_database()
    for mood in db.moods:
        if mood.id == mood_id:
            return mood.works
    raise HTTPException(status_code=404, detail=f"Categoria de mood com ID {mood_id} não encontrada")


@app.post("/moods/{mood_id}/works", response_model=Work, status_code=status.HTTP_201_CREATED, tags=["Moods"])
def add_work_to_mood(mood_id: int, work_in: WorkCreate):
    """Sugere e cadastra uma nova obra em uma categoria de humor existente."""
    db = load_database()
    target_mood: Optional[MoodCategory] = None
    all_work_ids = []

    for mood in db.moods:
        for w in mood.works:
            all_work_ids.append(w.id)
        if mood.id == mood_id:
            target_mood = mood

    if target_mood is None:
        raise HTTPException(status_code=404, detail=f"Categoria de mood com ID {mood_id} não encontrada")

    new_id = (max(all_work_ids) + 1) if all_work_ids else 1
    new_work = Work(
        id=new_id,
        title=work_in.title.strip(),
        type=work_in.type.strip(),
        description=work_in.description.strip()
    )

    target_mood.works.append(new_work)
    save_database(db)

    return new_work
