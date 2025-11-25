import {defineStore} from "pinia";
import {jwtDecode} from 'jwt-decode';

interface TokenPayload {
    exp: number;
    sub: string;
}

export const useAuthStore = defineStore('authStore', {
    state: () => ({
        token: "",
        email: "",
    }),
    getters: {
        isLoggedIn(state): boolean {
            if (!state.token) return false;
            try {
                const decoded = jwtDecode<TokenPayload>(state.token);
                const now = Date.now();
                const exp = decoded.exp * 1000;
                const remainingMs = exp - now;
                console.log(`⏳ Token vyprší za ${Math.floor(remainingMs / 1000)} sekund (${Math.round(remainingMs / 60000)} minut)`);
                if (remainingMs < 0) {
                    this.token = "";
                }
                return remainingMs > 0;
            } catch {
                return false;
            }
        }
    },
    actions: {
        logout() {
            this.token = "";
        }
    },
    persist: true,
})