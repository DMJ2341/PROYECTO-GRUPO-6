from pydantic import BaseModel, Field
from typing import List, Dict, Optional

class Reference(BaseModel):
    source: str = Field(..., example="NIST SP 800-12 Rev. 1, Section 2.1")  # Solo texto, no links

class Tab(BaseModel):
    id: str
    label: str
    title: Optional[str]
    content: Dict  # Flexible para text, stats, etc.

class Screen(BaseModel):
    screen_id: int = Field(..., ge=1)  # Validación: >0
    type: str = Field(..., example="story_hook")  # 'story_hook', 'deep_dive', 'lab', etc.
    title: str
    background_image: Optional[str]
    tabs: Optional[List[Tab]]  # Como en tu PDF
    content: Dict  # Para timeline, stats, etc.
    interactive: bool = Field(default=True)
    references: List[Reference]

class Lesson(BaseModel):
    course_id: int
    title: str
    screens: List[Screen]
    references: List[Reference]  # Referencias finales
    xp_reward: int = Field(..., ge=0)  # Validación: XP >= 0