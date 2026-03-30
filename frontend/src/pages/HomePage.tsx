import { upcomingEvents, latestNotice, careers } from '@/data/mockData'
import { RecentlyAccessedSection } from '@/components/home/RecentlyAccessedSection'
import { UpcomingEventsSection } from '@/components/home/UpcomingEventsSection'
import { LastNoticeSection } from '@/components/home/LastNoticeSection'
import { CareersCarousel } from '@/components/home/CareersCarousel'
import { useActivityStore } from '@/store/activityStore'
import { useInitActivity } from '@/hooks/useInitActivity'

export function HomePage() {
    useInitActivity()
    const recentItems = useActivityStore((s) => s.recentItems)

    return (
        <div className="flex flex-col gap-12 px-4 sm:px-[8%] pt-6 pb-10 animate-in fade-in slide-in-from-bottom-2 duration-300">
            {recentItems.length > 0 && <RecentlyAccessedSection items={recentItems.slice(0, 5)} />}
            <UpcomingEventsSection events={upcomingEvents} />
            <LastNoticeSection notice={latestNotice} />
            <CareersCarousel careers={careers} />
        </div>
    )
}
