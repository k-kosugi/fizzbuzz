export type Kind = 'fizzbuzz' | 'fizz' | 'buzz' | 'number'

export function kindOf(value: string): Kind {
  switch (value) {
    case 'FizzBuzz':
      return 'fizzbuzz'
    case 'Fizz':
      return 'fizz'
    case 'Buzz':
      return 'buzz'
    default:
      return 'number'
  }
}
