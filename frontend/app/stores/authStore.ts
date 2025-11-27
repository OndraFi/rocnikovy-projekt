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
        authToken(state): string | null {
            if (!state.token) return null;
    
            try {
                const decoded = jwtDecode<TokenPayload>(state.token);
                const now = Date.now();
                if (decoded.exp * 1000 < now) {
                    return null;
                }
                return state.token;
            } catch {
                return null;
            }
        }
    },
    actions: {
        isLoggedIn(): boolean {
            if (!this.token) return false;
            try {
                const decoded = jwtDecode<TokenPayload>(this.token);
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
        },
        logout() {
            this.token = "";
        },
        logIn(token: string){
            this.token = token;
        }
    },
    persist: true,
})