import { useCallback, useEffect, useRef, useState } from 'react'
import { ChevronLeft, ChevronRight, MessageCircle, Loader2 } from 'lucide-react'
import { cn } from '@/lib/utils'
import { forumDiscussionService } from '@/services/forumDiscussion.service'
import type { ForumDiscussionDTO } from '@/types/forum.types'

const SCROLL_AMOUNT = 220

function timeAgo(isoString: string): string {
    const diff = Date.now() - new Date(isoString).getTime()
    const minutes = Math.floor(diff / 60_000)
    if (minutes < 1) return 'ahora'
    if (minutes < 60) return `hace ${minutes} min`
    const hours = Math.floor(minutes / 60)
    if (hours < 24) return `hace ${hours}h`
    const days = Math.floor(hours / 24)
    return `hace ${days}d`
}

export function DiscussionsCarousel() {
    const scrollRef = useRef<HTMLDivElement>(null)
    const [canScrollLeft, setCanScrollLeft] = useState(false)
    const [canScrollRight, setCanScrollRight] = useState(false)
    const [discussions, setDiscussions] = useState<ForumDiscussionDTO[]>([])
    const [loading, setLoading] = useState(true)

    const updateArrows = useCallback(() => {
        const el = scrollRef.current
        if (!el) return
        setCanScrollLeft(el.scrollLeft > 4)
        setCanScrollRight(el.scrollLeft + el.clientWidth < el.scrollWidth - 4)
    }, [])

    useEffect(() => {
        forumDiscussionService
            .getRecent(8)
            .then(setDiscussions)
            .catch(() => setDiscussions([]))
            .finally(() => setLoading(false))
    }, [])

    useEffect(() => {
        updateArrows()
    }, [discussions, updateArrows])

    const scroll = (direction: 'left' | 'right') => {
        scrollRef.current?.scrollBy({
            left: direction === 'left' ? -SCROLL_AMOUNT : SCROLL_AMOUNT,
            behavior: 'smooth',
        })
    }

    const arrowBase = cn(
        'hidden lg:flex',
        'absolute top-1/2 -translate-y-1/2 z-10',
        'w-8 h-8 items-center justify-center rounded-full',
        'bg-background border border-border shadow-md',
        'text-foreground/70 hover:text-foreground hover:shadow-lg hover:scale-105',
        'transition-all duration-150',
    )

    if (loading) {
        return (
            <div className="flex items-center justify-center py-8">
                <Loader2 size={18} className="animate-spin text-muted-foreground" />
            </div>
        )
    }

    if (discussions.length === 0) {
        return (
            <div className="flex flex-col items-center justify-center gap-2 py-8 text-center">
                <MessageCircle size={24} className="text-muted-foreground/40" />
                <p className="text-sm text-muted-foreground">No hay discusiones activas</p>
            </div>
        )
    }

    return (
        <div className="relative">
            <button
                onClick={() => scroll('left')}
                aria-label="Anterior"
                className={cn(arrowBase, '-left-4', !canScrollLeft && 'opacity-0 pointer-events-none')}
            >
                <ChevronLeft size={16} />
            </button>

            <button
                onClick={() => scroll('right')}
                aria-label="Siguiente"
                className={cn(arrowBase, '-right-4', !canScrollRight && 'opacity-0 pointer-events-none')}
            >
                <ChevronRight size={16} />
            </button>

            <div className="-mx-4 px-4 sm:-mx-6 sm:px-6 lg:mx-0 lg:px-0">
                <div
                    ref={scrollRef}
                    onScroll={updateArrows}
                    className="flex gap-3 overflow-x-auto pb-3 snap-x snap-mandatory scrollbar-hide"
                >
                    {discussions.map((discussion) => (
                        <div
                            key={discussion.id}
                            className={cn(
                                'snap-start shrink-0 w-48 sm:w-52',
                                'flex flex-col gap-2.5 rounded-xl border border-border bg-card p-4',
                                'hover:border-primary/30 hover:shadow-md transition-all duration-200 cursor-pointer',
                            )}
                        >
                            {/* Status badge */}
                            <span className={cn(
                                'self-start text-[10px] px-2 py-0.5 rounded-full font-semibold',
                                discussion.open
                                    ? 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-400'
                                    : 'bg-muted text-muted-foreground',
                            )}>
                                {discussion.open ? 'Abierto' : 'Cerrado'}
                            </span>

                            {/* Title */}
                            <p className="text-sm font-semibold text-foreground line-clamp-3 leading-snug flex-1">
                                {discussion.title}
                            </p>

                            {/* Time */}
                            <span className="text-[11px] text-muted-foreground mt-auto">
                                {timeAgo(discussion.updatedAt)}
                            </span>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    )
}
