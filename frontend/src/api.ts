export interface FizzBuzz {
  number: number
  value: string
}

export class ApiError extends Error {
  readonly status: number

  constructor(status: number, message: string) {
    super(message)
    this.status = status
  }
}

const BASE_PATH = '/api/fizzbuzz'

async function toApiError(response: Response): Promise<ApiError> {
  try {
    const problem = (await response.json()) as { detail?: string }
    return new ApiError(response.status, problem.detail ?? response.statusText)
  } catch {
    return new ApiError(response.status, response.statusText)
  }
}

async function send(path: string, init?: RequestInit): Promise<FizzBuzz> {
  let response: Response
  try {
    response = await fetch(path, { ...init, headers: { Accept: 'application/json', ...init?.headers } })
  } catch {
    throw new ApiError(0, 'サーバーに接続できません')
  }
  if (!response.ok) {
    throw await toApiError(response)
  }
  return (await response.json()) as FizzBuzz
}

export function registerFizzBuzz(number: number): Promise<FizzBuzz> {
  return send(BASE_PATH, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ number }),
  })
}

export function findFizzBuzz(number: number): Promise<FizzBuzz> {
  return send(`${BASE_PATH}/${number}`)
}
