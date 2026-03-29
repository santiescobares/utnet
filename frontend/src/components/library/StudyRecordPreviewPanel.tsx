import '@/lib/pdfWorker'
import 'react-pdf/dist/Page/AnnotationLayer.css'
import 'react-pdf/dist/Page/TextLayer.css'

import { useEffect, useRef, useState } from 'react'
import { Document, Page } from 'react-pdf'
import { ChevronLeft, ChevronRight, Loader2, FileX } from 'lucide-react'
import { cn } from '@/lib/utils'

type FileType = 'pdf' | 'image' | 'word' | null

function detectFileType(url: string): FileType {
    const ext = url.split('?')[0].split('.').pop()?.toLowerCase()
    if (ext === 'pdf') return 'pdf'
    if (['png', 'jpg', 'jpeg'].includes(ext ?? '')) return 'image'
    if (['doc', 'docx'].includes(ext ?? '')) return 'word'
    return null
}

interface StudyRecordPreviewPanelProps {
    previewUrl: string | null
}

export function StudyRecordPreviewPanel({ previewUrl }: StudyRecordPreviewPanelProps) {
    const containerRef = useRef<HTMLDivElement>(null)
    const [containerWidth, setContainerWidth] = useState<number>(0)
    const [fileType, setFileType] = useState<FileType>(null)
    const [numPages, setNumPages] = useState(0)
    const [currentPage, setCurrentPage] = useState(1)
    const [pdfError, setPdfError] = useState(false)

    // Measure container width for react-pdf Page
    useEffect(() => {
        const el = containerRef.current
        if (!el) return
        const observer = new ResizeObserver(([entry]) => {
            setContainerWidth(entry.contentRect.width)
        })
        observer.observe(el)
        setContainerWidth(el.offsetWidth)
        return () => observer.disconnect()
    }, [])

    // Detect file type whenever URL changes
    useEffect(() => {
        if (!previewUrl) return
        setFileType(detectFileType(previewUrl))
        setCurrentPage(1)
        setNumPages(0)
        setPdfError(false)
    }, [previewUrl])

    const maxPages = fileType === 'pdf' ? Math.min(numPages, 10) : 1
    const canGoPrev = currentPage > 1
    const canGoNext = fileType === 'pdf' && currentPage < maxPages

    // Non-PDF content needs centering; PDF scrolls naturally
    const isPdf = fileType === 'pdf'

    return (
        <div className="flex flex-col h-full">
            <h2 className="text-sm font-semibold text-foreground text-center py-3 border-b border-border shrink-0">
                Vista Previa
            </h2>

            {/* Preview content */}
            <div
                ref={containerRef}
                className={cn(
                    'flex-1 overflow-y-auto mx-3 mt-3 rounded-2xl border border-border bg-card',
                    !isPdf && 'flex items-center justify-center min-h-[200px]',
                )}
            >
                {!previewUrl && (
                    <div className="flex items-center justify-center h-full min-h-[200px]">
                        <Loader2 className="animate-spin text-muted-foreground" size={28} />
                    </div>
                )}

                {previewUrl && fileType === 'pdf' && !pdfError && (
                    <Document
                        file={previewUrl}
                        onLoadSuccess={({ numPages }) => setNumPages(numPages)}
                        onLoadError={() => setPdfError(true)}
                        loading={
                            <div className="flex items-center justify-center min-h-[200px]">
                                <Loader2 className="animate-spin text-muted-foreground" size={28} />
                            </div>
                        }
                        className="w-full"
                    >
                        <Page
                            pageNumber={currentPage}
                            width={containerWidth > 0 ? containerWidth : undefined}
                            renderTextLayer={false}
                            renderAnnotationLayer={false}
                        />
                    </Document>
                )}

                {previewUrl && fileType === 'pdf' && pdfError && (
                    <div className="flex flex-col items-center justify-center gap-2 min-h-[200px] text-center px-4">
                        <FileX className="text-muted-foreground" size={32} />
                        <p className="text-sm text-muted-foreground">No se pudo cargar la vista previa.</p>
                    </div>
                )}

                {previewUrl && fileType === 'image' && (
                    <img
                        src={previewUrl}
                        alt="Vista previa"
                        className="w-full h-full object-contain rounded-2xl"
                    />
                )}

                {previewUrl && fileType === 'word' && (
                    <div className="flex items-center justify-center h-full px-6 text-center">
                        <p className="text-sm text-muted-foreground leading-relaxed">
                            No es posible previsualizar este archivo.<br />
                            Descargalo para obtener una vista completa.
                        </p>
                    </div>
                )}

                {previewUrl && fileType === null && (
                    <div className="flex items-center justify-center h-full min-h-[200px]">
                        <Loader2 className="animate-spin text-muted-foreground" size={28} />
                    </div>
                )}
            </div>

            {/* Page navigation */}
            <div className="flex flex-col items-center gap-1 py-3 shrink-0">
                <div className="flex items-center gap-4">
                    <button
                        onClick={() => setCurrentPage((p) => p - 1)}
                        disabled={!canGoPrev}
                        className={cn(
                            'p-1.5 rounded-lg transition-colors',
                            canGoPrev
                                ? 'hover:bg-secondary text-foreground'
                                : 'text-muted-foreground/40 cursor-not-allowed',
                        )}
                        aria-label="Página anterior"
                    >
                        <ChevronLeft size={18} />
                    </button>

                    <span className="text-sm font-medium text-foreground tabular-nums">
                        {fileType === 'pdf' && numPages > 0
                            ? `${currentPage}/${maxPages}`
                            : fileType !== null
                            ? '1/1'
                            : '—'}
                    </span>

                    <button
                        onClick={() => setCurrentPage((p) => p + 1)}
                        disabled={!canGoNext}
                        className={cn(
                            'p-1.5 rounded-lg transition-colors',
                            canGoNext
                                ? 'hover:bg-secondary text-foreground'
                                : 'text-muted-foreground/40 cursor-not-allowed',
                        )}
                        aria-label="Página siguiente"
                    >
                        <ChevronRight size={18} />
                    </button>
                </div>

                {fileType === 'pdf' && numPages > 10 && (
                    <p className="text-[11px] text-muted-foreground text-center px-4">
                        Descargá el recurso para tener una vista completa
                    </p>
                )}
            </div>
        </div>
    )
}
