import { useEffect, useRef, useState, type FormEvent, type ReactNode } from 'react'
import { ApiError, type FizzBuzz } from '../api'
import { ResultBadge } from './ResultBadge'

const MAX_NUMBER = 2147483647

type State =
  | { status: 'idle' }
  | { status: 'loading' }
  | { status: 'success'; result: FizzBuzz; sequence: number }
  | { status: 'error'; message: string }

interface Props {
  icon: ReactNode
  title: string
  description: string
  buttonLabel: string
  action: (number: number) => Promise<FizzBuzz>
  onSuccess?: (result: FizzBuzz) => void
  messageFor: (error: ApiError) => string
  // 参照が変わるたびに、その数値を入力して実行する
  preset?: { value: number }
}

function parseNumber(text: string): number | undefined {
  if (!/^\d+$/.test(text)) {
    return undefined
  }
  const number = Number(text)
  return number >= 1 && number <= MAX_NUMBER ? number : undefined
}

export function ActionCard({ icon, title, description, buttonLabel, action, onSuccess, messageFor, preset }: Props) {
  const [text, setText] = useState('')
  const [state, setState] = useState<State>({ status: 'idle' })
  const sequence = useRef(0)

  async function run(number: number) {
    setState({ status: 'loading' })
    try {
      const result = await action(number)
      sequence.current += 1
      setState({ status: 'success', result, sequence: sequence.current })
      onSuccess?.(result)
    } catch (error) {
      const message = error instanceof ApiError ? messageFor(error) : '予期しないエラーが発生しました'
      setState({ status: 'error', message })
    }
  }

  useEffect(() => {
    if (preset) {
      setText(String(preset.value))
      void run(preset.value)
    }
    // preset が変わったときだけ実行する
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [preset])

  function handleSubmit(event: FormEvent) {
    event.preventDefault()
    const number = parseNumber(text.trim())
    if (number === undefined) {
      setState({ status: 'error', message: `1 から ${MAX_NUMBER.toLocaleString()} の整数を入力してください` })
      return
    }
    void run(number)
  }

  return (
    <section className="card">
      <div className="card-header">
        <span className="card-icon" aria-hidden="true">{icon}</span>
        <div>
          <h2>{title}</h2>
          <p>{description}</p>
        </div>
      </div>

      <form className="form" onSubmit={handleSubmit}>
        <input
          className="input"
          inputMode="numeric"
          placeholder="数値を入力 (例: 15)"
          aria-label={`${title}: 数値`}
          value={text}
          onChange={(event) => setText(event.target.value)}
        />
        <button className="button" type="submit" disabled={state.status === 'loading'}>
          {state.status === 'loading' ? <span className="spinner" aria-label="処理中" /> : buttonLabel}
        </button>
      </form>

      <div className="result-area" aria-live="polite">
        {state.status === 'success' && <ResultBadge key={state.sequence} result={state.result} />}
        {state.status === 'error' && <p className="error" role="alert">{state.message}</p>}
      </div>
    </section>
  )
}
