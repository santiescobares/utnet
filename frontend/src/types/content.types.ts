// Tipo de ítem reciente — 'apunte' es placeholder para feature futura de Biblioteca
export type ContentItemType = 'course' | 'forum' | 'apunte'

export type EventType = 'presencial' | 'virtual' | 'hibrido'

export type CareerType = 'presencial' | 'virtual' | 'hibrido'

// Mapea a: LogDTO (action=OPEN/CREATE) + recurso referenciado según resourceType
export interface RecentItem {
    id: string
    type: ContentItemType
    title: string
    subtitle: string
    accessedAt: string // ISO 8601
    href: string
}

// Mapea a: CourseEventDTO + courseName para display
export interface UpcomingEvent {
    id: string
    title: string
    description: string
    date: string // ISO 8601 (YYYY-MM-DD)
    startTime?: string // HH:mm
    location: string
    type: EventType
}

// Mapea a: ForumDiscussionDTO del topic "Avisos" + primer ForumThreadDTO (contenido)
export interface ForumNotice {
    id: string
    title: string
    content: string
    author: string
    postedAt: string // ISO 8601
}

// Mapea a: CareerDTO + datos enriquecidos estáticos por idCharacter
// Nombre CareerInfo para no colisionar con CareerDTO de user.types.ts
export interface CareerInfo {
    id: number
    name: string
    faculty: string
    durationYears: number
    type: CareerType
    description: string
}
