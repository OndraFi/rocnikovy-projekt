import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useAuthStore } from '../../app/stores/authStore'
import { jwtDecode } from 'jwt-decode'
import { UserResponseRoleEnum } from '../../api'

vi.mock('jwt-decode', () => ({
  jwtDecode: vi.fn(),
}))

const mockedJwtDecode = vi.mocked(jwtDecode)

describe('authStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('returns authToken when token is valid', () => {
    const nowMs = 1_700_000_000_000
    vi.spyOn(Date, 'now').mockReturnValue(nowMs)
    mockedJwtDecode.mockReturnValue({
      exp: Math.floor(nowMs / 1000) + 60,
      sub: 'user',
    })

    const store = useAuthStore()
    store.token = 'token'

    expect(store.authToken).toBe('token')
  })

  it('returns null authToken when token is expired', () => {
    const nowMs = 1_700_000_000_000
    vi.spyOn(Date, 'now').mockReturnValue(nowMs)
    mockedJwtDecode.mockReturnValue({
      exp: Math.floor(nowMs / 1000) - 1,
      sub: 'user',
    })

    const store = useAuthStore()
    store.token = 'token'

    expect(store.authToken).toBeNull()
  })

  it('clears token on isLoggedIn when expired', () => {
    const nowMs = 1_700_000_000_000
    vi.spyOn(Date, 'now').mockReturnValue(nowMs)
    mockedJwtDecode.mockReturnValue({
      exp: Math.floor(nowMs / 1000) - 1,
      sub: 'user',
    })
    vi.spyOn(console, 'log').mockImplementation(() => {})

    const store = useAuthStore()
    store.token = 'token'

    expect(store.isLoggedIn()).toBe(false)
    expect(store.token).toBe('')
  })

  it('detects admin and chief roles', () => {
    const store = useAuthStore()
    store.setUser({ role: UserResponseRoleEnum.Admin })
    expect(store.isAdminOrChief()).toBe(true)

    store.setUser({ role: UserResponseRoleEnum.ChiefEditor })
    expect(store.isAdminOrChief()).toBe(true)

    store.setUser({ role: UserResponseRoleEnum.User })
    expect(store.isAdminOrChief()).toBe(false)
  })
})
