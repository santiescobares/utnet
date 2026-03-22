import { recentItems, upcomingEvents, latestNotice, careers } from '@/data/mockData'
import { RecentlyAccessedSection } from '@/components/home/RecentlyAccessedSection'
import { UpcomingEventsSection } from '@/components/home/UpcomingEventsSection'
import { LastNoticeSection } from '@/components/home/LastNoticeSection'
import { CareersCarousel } from '@/components/home/CareersCarousel'

export function HomePage() {
    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 flex flex-col gap-12">
            {recentItems.length > 0 && <RecentlyAccessedSection items={recentItems} />}
            <UpcomingEventsSection events={upcomingEvents} />
            <LastNoticeSection notice={latestNotice} />
            <CareersCarousel careers={careers} />
        </div>
    )
}
