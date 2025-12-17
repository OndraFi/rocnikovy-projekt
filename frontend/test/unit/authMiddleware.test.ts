import { afterEach, describe, expect, it, vi } from 'vitest'

const loadMiddleware = async (isLoggedIn: boolean) => {
  vi.resetModules()
  vi.stubGlobal('defineNuxtRouteMiddleware', (fn: any) => fn)
  const navigateTo = vi.fn(() => 'redirected')
  vi.stubGlobal('navigateTo', navigateTo)
  vi.stubGlobal('useAuthStore', () => ({ isLoggedIn: () => isLoggedIn }))

  const middleware = (await import('../../app/middleware/auth.global')).default
  return { middleware, navigateTo }
}

afterEach(() => {
  vi.unstubAllGlobals()
})

describe('auth.global middleware', () => {
  it('redirects to login when unauthenticated', async () => {
    const { middleware, navigateTo } = await loadMiddleware(false)
    const result = middleware({ path: '/dashboard' } as any, { path: '/' } as any)

    expect(navigateTo).toHaveBeenCalledWith('/dashboard/login')
    expect(result).toBe('redirected')
  })

  it('does not redirect when already logged in', async () => {
    const { middleware, navigateTo } = await loadMiddleware(true)
    const result = middleware({ path: '/dashboard' } as any, { path: '/' } as any)

    expect(navigateTo).not.toHaveBeenCalled()
    expect(result).toBeUndefined()
  })

  it('does not redirect on the login route', async () => {
    const { middleware, navigateTo } = await loadMiddleware(false)
    middleware({ path: '/dashboard/login' } as any, { path: '/' } as any)

    expect(navigateTo).not.toHaveBeenCalled()
  })
})
