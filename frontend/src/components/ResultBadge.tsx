import type { FizzBuzz } from '../api'
import { kindOf } from '../kind'

export function ResultBadge({ result }: { result: FizzBuzz }) {
  return (
    <div className={`badge badge-${kindOf(result.value)}`} role="status">
      <span className="badge-id">#{result.number.toLocaleString()}</span>
      <span className="badge-value">{result.value}</span>
    </div>
  )
}
