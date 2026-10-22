import { useState } from 'react'
import { ApiError, findFizzBuzz, registerFizzBuzz, type FizzBuzz } from './api'
import { ActionCard } from './components/ActionCard'
import { kindOf } from './kind'
import './App.css'

const MAX_HISTORY = 12

function registerMessage(error: ApiError): string {
  if (error.status === 400) return '数値が正しくありません。1 以上の整数を指定してください'
  if (error.status === 0) return error.message
  return `保存に失敗しました (${error.status})`
}

function findMessage(error: ApiError): string {
  if (error.status === 404) return 'この数値はまだ保存されていません'
  if (error.status === 400) return '数値が正しくありません'
  if (error.status === 0) return error.message
  return `取得に失敗しました (${error.status})`
}

export default function App() {
  const [history, setHistory] = useState<FizzBuzz[]>([])
  const [lookup, setLookup] = useState<{ value: number } | undefined>(undefined)

  function remember(result: FizzBuzz) {
    setHistory((current) => [result, ...current.filter((item) => item.number !== result.number)].slice(0, MAX_HISTORY))
  }

  return (
    <>
      <div className="backdrop" aria-hidden="true">
        <span className="blob blob-a" />
        <span className="blob blob-b" />
        <span className="blob blob-c" />
      </div>

      <main className="container">
        <header className="hero">
          <p className="eyebrow">Spring Boot x React</p>
          <h1>
            <span className="gradient-text">FizzBuzz</span> Studio
          </h1>
          <p className="lead">数値を保存して、いつでも取り出せる。3 の倍数は Fizz、5 の倍数は Buzz、両方なら FizzBuzz。</p>
        </header>

        <div className="grid">
          <ActionCard
            icon="✦"
            title="保存する"
            description="数値を 1 件ずつ保存します"
            buttonLabel="保存"
            action={registerFizzBuzz}
            onSuccess={remember}
            messageFor={registerMessage}
          />
          <ActionCard
            icon="⌕"
            title="取得する"
            description="保存済みの数値を取り出します"
            buttonLabel="取得"
            action={findFizzBuzz}
            messageFor={findMessage}
            preset={lookup}
          />
        </div>

        <section className="history">
          <h3>最近保存した数値</h3>
          {history.length === 0 ? (
            <p className="muted">まだありません。数値を保存してみましょう。</p>
          ) : (
            <ul className="chips">
              {history.map((item) => (
                <li key={item.number}>
                  <button
                    type="button"
                    className={`chip chip-${kindOf(item.value)}`}
                    onClick={() => setLookup({ value: item.number })}
                    title="クリックで取得"
                  >
                    <span className="chip-id">{item.number.toLocaleString()}</span>
                    <span className="chip-value">{item.value}</span>
                  </button>
                </li>
              ))}
            </ul>
          )}
        </section>

        <footer className="footer">データはサーバーのメモリ上に保存され、再起動すると消えます。</footer>
      </main>
    </>
  )
}
