import { useCallback, useEffect, useRef, useState } from 'react'
import { ChevronLeft, ChevronRight } from 'lucide-react'
import { cn } from '@/lib/utils'
import { SectionHeader } from '@/components/ui/SectionHeader'
import type { CareerInfo, CareerType } from '@/types/content.types'

interface CareersCarouselProps {
    careers: CareerInfo[]
}

const typeLabelMap: Record<CareerType, string> = {
    presencial: 'Presencial',
    virtual: 'Virtual',
    hibrido: 'Híbrido',
}

const typeBadgeClass: Record<CareerType, string> = {
    presencial: 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-400',
    virtual: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
    hibrido: 'bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-400',
}

// Pixels to scroll per arrow click (roughly one card width)
const SCROLL_AMOUNT = 232

export function CareersCarousel({ careers }: CareersCarouselProps) {
    const scrollRef = useRef<HTMLDivElement>(null)
    const [canScrollLeft, setCanScrollLeft] = useState(false)
    const [canScrollRight, setCanScrollRight] = useState(false)

    const updateArrows = useCallback(() => {
        const el = scrollRef.current
        if (!el) return
        setCanScrollLeft(el.scrollLeft > 4)
        setCanScrollRight(el.scrollLeft + el.clientWidth < el.scrollWidth - 4)
    }, [])

    // Run on mount and whenever careers change
    useEffect(() => {
        updateArrows()
    }, [careers, updateArrows])

    const scroll = (direction: 'left' | 'right') => {
        const el = scrollRef.current
        if (!el) return
        el.scrollBy({ left: direction === 'left' ? -SCROLL_AMOUNT : SCROLL_AMOUNT, behavior: 'smooth' })
    }

    const arrowBase = cn(
        // Hidden on touch devices (they scroll natively), visible on pointer devices
        'hidden lg:flex',
        'absolute top-1/2 -translate-y-1/2 z-10',
        'w-9 h-9 items-center justify-center rounded-full',
        'bg-background border border-border shadow-md',
        'text-foreground/70 hover:text-foreground hover:shadow-lg hover:scale-105',
        'transition-all duration-150',
    )

    return (
        <section className="animate-in fade-in slide-in-from-bottom-2 duration-300">
            <SectionHeader title="Carreras" />

            {/* Wrapper: relative so arrows can be positioned absolutely */}
            <div className="relative">
                {/* Left arrow */}
                <button
                    onClick={() => scroll('left')}
                    aria-label="Anterior"
                    className={cn(arrowBase, '-left-4', !canScrollLeft && 'opacity-0 pointer-events-none')}
                >
                    <ChevronLeft size={18} />
                </button>

                {/* Right arrow */}
                <button
                    onClick={() => scroll('right')}
                    aria-label="Siguiente"
                    className={cn(arrowBase, '-right-4', !canScrollRight && 'opacity-0 pointer-events-none')}
                >
                    <ChevronRight size={18} />
                </button>

                {/* Carousel track: bleeds to edges on mobile, contained on desktop */}
                <div className="-mx-4 px-4 sm:-mx-6 sm:px-6 lg:mx-0 lg:px-0">
                    <div
                        ref={scrollRef}
                        onScroll={updateArrows}
                        className="flex gap-3 overflow-x-auto pb-3 snap-x snap-mandatory scrollbar-hide"
                    >
                        {careers.map((career) => (
                            <div
                                key={career.id}
                                className={cn(
                                    'snap-start shrink-0 w-52 sm:w-56',
                                    'flex flex-col gap-3 rounded-xl border border-border bg-card p-4',
                                    'hover:border-primary/30 hover:shadow-md transition-all duration-200 cursor-pointer',
                                )}
                            >
                                {/* Career name */}
                                <div>
                                    <h3 className="font-semibold text-sm text-foreground line-clamp-2 leading-snug">
                                        {career.name}
                                    </h3>
                                    <span className="text-[11px] text-muted-foreground mt-1 block">{career.faculty}</span>
                                </div>

                                {/* Description */}
                                <p className="text-xs text-muted-foreground line-clamp-3 leading-relaxed flex-1">
                                    {career.description}
                                </p>

                                {/* Footer: duration + type badge */}
                                <div className="flex items-center justify-between gap-2 flex-wrap mt-auto">
                                    <span className="text-xs text-muted-foreground font-medium">
                                        {career.durationYears} años
                                    </span>
                                    <span
                                        className={cn(
                                            'text-[10px] px-2 py-0.5 rounded-full font-semibold min-w-[5.5rem] text-center inline-block',
                                            typeBadgeClass[career.type],
                                        )}
                                    >
                                        {typeLabelMap[career.type]}
                                    </span>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </section>
    )
}
