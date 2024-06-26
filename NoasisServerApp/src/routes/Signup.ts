import bcrypt from 'bcrypt'
import validator from 'email-validator'
import { Request, Response } from 'express'
import register from './queries/Register'

export default async function Signup(req: Request, res: Response) {
  try {
    const email = req.body.email
    const password = req.body.password

    if (!email) {
      return res.status(400).json({
        error: 'Email not provided.',
      })
    }

    if (!password) {
      return res.status(400).json({
        error: 'Password not provided.',
      })
    }

    if (!validator.validate(email)) {
      return res.status(400).json({
        error: 'Invalid email.',
      })
    }

    const SALT_ROUNDS = 10
    const hashedPassword = await bcrypt.hash(password, SALT_ROUNDS)
    if (!hashedPassword) {
      return res.status(500).json({
        error: 'Failed to generate password hash.',
      })
    }

    await register(email, hashedPassword)
    return res.status(200).json({
      message: 'Successfully registered.',
    })

  } catch (err: any) {
    if (err.errno === 1062) {
      return res.status(409).json({
        error: 'Email already exists.',
      })
    }

    console.error(err)
    return res.sendStatus(500)
  }
}
