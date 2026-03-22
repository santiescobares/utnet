import { useState } from 'react'
import { Outlet } from 'react-router'
import { TopNavbar } from '@/components/layout/TopNavbar'
import { Sidebar } from '@/components/layout/Sidebar'
import { Footer } from '@/components/layout/Footer'

export function MainLayout() {
    const [isSidebarOpen, setSidebarOpen] = useState(false)

    return (
        <div className="flex h-screen overflow-hidden bg-background">
            <Sidebar isOpen={isSidebarOpen} onClose={() => setSidebarOpen(false)} />

            {/* Right column scrolls as a whole — navbar sticky inside it, footer flows after content */}
            <div className="flex flex-1 flex-col min-w-0 overflow-y-auto">
                <TopNavbar onMenuToggle={() => setSidebarOpen((p) => !p)} />

                <main className="flex-1">
                    <Outlet />
                </main>

                <Footer />
            </div>
        </div>
    )
}
