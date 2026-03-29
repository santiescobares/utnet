import { createBrowserRouter, Navigate } from 'react-router'
import { LandingPage } from '@/pages/LandingPage'
import { MainLayout } from '@/layouts/MainLayout'
import { HomePage } from '@/pages/HomePage'
import { CoursesPage } from '@/pages/CoursesPage'
import { CourseDetailPage } from '@/pages/CourseDetailPage'
import { LibraryPage } from '@/pages/LibraryPage'
import { NewResourcePage } from '@/pages/NewResourcePage'
import { useAuthStore } from '@/store/authStore'

function ProtectedRoute({ children }: { children: React.ReactNode }) {
    const { isAuthenticated } = useAuthStore()
    if (!isAuthenticated) {
        return <Navigate replace to="/" />
    }
    return <>{children}</>
}

export const router = createBrowserRouter([
    {
        path: '/',
        element: <LandingPage />,
    },
    {
        path: '/',
        element: (
            <ProtectedRoute>
                <MainLayout />
            </ProtectedRoute>
        ),
        children: [
            {
                path: 'home',
                element: <HomePage />,
            },
            {
                path: 'courses',
                element: <CoursesPage />,
            },
            {
                path: 'courses/:courseName',
                element: <CourseDetailPage />,
            },
            {
                path: 'library',
                element: <LibraryPage />,
            },
            {
                path: 'library/new-resource',
                element: <NewResourcePage />,
            },
        ],
    },
])
