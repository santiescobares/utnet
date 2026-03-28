import { useEffect, useState } from 'react'
import {
    DndContext,
    DragOverlay,
    MouseSensor,
    TouchSensor,
    closestCenter,
    useSensor,
    useSensors,
    type DragStartEvent,
    type DragEndEvent,
} from '@dnd-kit/core'
import {
    SortableContext,
    rectSortingStrategy,
    useSortable,
} from '@dnd-kit/sortable'
import { CSS } from '@dnd-kit/utilities'
import { cn } from '@/lib/utils'
import { CourseCard } from './CourseCard'
import type { CourseDTO } from '@/types/course.types'

interface SavedCoursesSectionProps {
    courses: CourseDTO[]
    careerMap: Map<number, string>
    careerColorMap?: Map<number, string>
    onReorder: (fromIndex: number, toIndex: number) => void
    onBookmarkToggle?: (courseId: number) => void
    onCardClick?: (course: CourseDTO) => void
}

function useIsMobile() {
    const [isMobile, setIsMobile] = useState(() => window.innerWidth < 640)
    useEffect(() => {
        const mq = window.matchMedia('(max-width: 639px)')
        const handler = (e: MediaQueryListEvent) => setIsMobile(e.matches)
        mq.addEventListener('change', handler)
        return () => mq.removeEventListener('change', handler)
    }, [])
    return isMobile
}

interface SortableCardProps {
    course: CourseDTO
    careerName: string
    careerColor?: string
    isCarousel: boolean
    onBookmarkToggle?: (courseId: number) => void
    onCardClick?: (course: CourseDTO) => void
}

function SortableCard({ course, careerName, careerColor, isCarousel, onBookmarkToggle, onCardClick }: SortableCardProps) {
    const { attributes, listeners, setNodeRef, transform, transition, isDragging } =
        useSortable({ id: course.id })

    return (
        <div
            ref={setNodeRef}
            style={{ transform: CSS.Transform.toString(transform), transition }}
            className={cn(
                isCarousel && 'snap-start shrink-0 w-52',
                isDragging && 'opacity-30',
            )}
        >
            <CourseCard
                course={course}
                careerName={careerName}
                careerColor={careerColor}
                isDraggable
                isBookmarked
                onClick={onCardClick ? () => onCardClick(course) : undefined}
                onBookmarkToggle={onBookmarkToggle ? (e) => { e.stopPropagation(); onBookmarkToggle(course.id) } : undefined}
                // En carousel: cancela translate para que no se vea raro en touch
                className={cn(isCarousel && 'hover:translate-y-0 active:scale-100')}
                dragHandleProps={{ ...attributes, ...listeners }}
            />
        </div>
    )
}

export function SavedCoursesSection({ courses, careerMap, careerColorMap, onReorder, onBookmarkToggle, onCardClick }: SavedCoursesSectionProps) {
    const [activeId, setActiveId] = useState<number | null>(null)
    const isMobile = useIsMobile()

    const sensors = useSensors(
        useSensor(MouseSensor, {
            activationConstraint: { distance: 5 },
        }),
        useSensor(TouchSensor, {
            // 250ms hold sin mover > 5px → activa drag; antes → scroll normal
            activationConstraint: { delay: 250, tolerance: 5 },
        }),
    )

    const activeCourse = courses.find((c) => c.id === activeId) ?? null

    const handleDragStart = ({ active }: DragStartEvent) => {
        setActiveId(active.id as number)
    }

    const handleDragEnd = ({ active, over }: DragEndEvent) => {
        setActiveId(null)
        if (!over || active.id === over.id) return
        const from = courses.findIndex((c) => c.id === active.id)
        const to = courses.findIndex((c) => c.id === over.id)
        if (from !== -1 && to !== -1) onReorder(from, to)
    }

    const handleDragCancel = () => setActiveId(null)

    const itemIds = courses.map((c) => c.id)

    return (
        <DndContext
            sensors={sensors}
            collisionDetection={closestCenter}
            onDragStart={handleDragStart}
            onDragEnd={handleDragEnd}
            onDragCancel={handleDragCancel}
        >
            <SortableContext items={itemIds} strategy={rectSortingStrategy}>
                {isMobile ? (
                    <div className="-mx-4 px-4">
                        <div className="flex gap-3 overflow-x-auto pb-3 snap-x snap-mandatory scrollbar-hide">
                            {courses.map((course) => (
                                <SortableCard
                                    key={course.id}
                                    course={course}
                                    careerName={careerMap.get(course.careerId) ?? ''}
                                    careerColor={careerColorMap?.get(course.careerId)}
                                    isCarousel
                                    onBookmarkToggle={onBookmarkToggle}
                                    onCardClick={onCardClick}
                                />
                            ))}
                        </div>
                    </div>
                ) : (
                    <div className="grid grid-cols-2 lg:grid-cols-3 gap-4">
                        {courses.map((course) => (
                            <SortableCard
                                key={course.id}
                                course={course}
                                careerName={careerMap.get(course.careerId) ?? ''}
                                careerColor={careerColorMap?.get(course.careerId)}
                                isCarousel={false}
                                onBookmarkToggle={onBookmarkToggle}
                                onCardClick={onCardClick}
                            />
                        ))}
                    </div>
                )}
            </SortableContext>

            {/* Tarjeta fantasma que sigue al cursor/dedo durante el drag */}
            <DragOverlay dropAnimation={null}>
                {activeCourse && (
                    <div className={cn(isMobile && 'w-52')}>
                        <CourseCard
                            course={activeCourse}
                            careerName={careerMap.get(activeCourse.careerId) ?? ''}
                            careerColor={careerColorMap?.get(activeCourse.careerId)}
                            isDraggable
                            className="opacity-75 shadow-2xl scale-105 cursor-grabbing"
                        />
                    </div>
                )}
            </DragOverlay>
        </DndContext>
    )
}
