import { create } from 'zustand';
import type { RecentItem, ContentItemType } from '@/types/content.types';

interface ActivityState {
    recentItems: RecentItem[];
    initialized: boolean;
    setItems: (items: RecentItem[]) => void;
    addItem: (item: RecentItem) => void;
}

export const useActivityStore = create<ActivityState>()((set) => ({
    recentItems: [],
    initialized: false,

    setItems: (items) => set({ recentItems: items, initialized: true }),

    addItem: (item) =>
        set((state) => {
            // Remove existing entry with same id + type (dedup)
            const filtered = state.recentItems.filter(
                (i) => !(i.id === item.id && i.type === item.type)
            );
            // Prepend the new item
            const prepended = [item, ...filtered];
            // Enforce max 5 per ContentItemType
            const counts: Partial<Record<ContentItemType, number>> = {};
            const capped = prepended.filter((i) => {
                counts[i.type] = (counts[i.type] ?? 0) + 1;
                return counts[i.type]! <= 5;
            });
            return { recentItems: capped };
        }),
}));
