# backend/models/lesson_schema.py
from pydantic import BaseModel, Field
from typing import List, Dict, Optional

class Reference(BaseModel):
    source: str = Field(..., example="NIST SP 800-12 Rev. 1, Section 2.1")  # Solo texto

class Tab(BaseModel):
    id: str
    label: str
    title: Optional[str]
    content: Dict  # Para text, stats, timeline

class Screen(BaseModel):
    screen_id: int = Field(..., ge=1)  # >0
    type: str = Field(..., example="story_hook")  # 'story_hook', 'deep_dive', 'lab'
    title: str
    background_image: Optional[str]
    tabs: Optional[List[Tab]]  # Como en tu PDF
    content: Dict  # Para timeline, stats, etc.
    interactive: bool = Field(default=True)
    references: List[Reference]

class Lesson(BaseModel):
    id: str = Field(..., example="fundamentos_leccion_1")
    course_id: int
    title: str
    description: Optional[str]
    type: Optional[str]
    content: Dict  # Todo el JSONB
    screens: List[Screen]
    total_screens: int = Field(default=0, ge=0)
    duration_minutes: int = Field(default=15, ge=1)
    xp_reward: int = Field(default=50, ge=0)
    order_index: int = Field(..., ge=1)
    references: List[Reference]  # Al final, solo texto