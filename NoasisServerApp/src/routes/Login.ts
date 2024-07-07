import validator from 'email-validator'
import { Request, Response } from 'express'
import getUserId from './queries/GetUserId'
import login from './queries/Login'

export default async function Login(req: Request, res: Response) {
  try {
    const email = req.body.email
    const password = req.body.password

    console.log(req.body)
    const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))
    // await delay(2000)

    if (!email) {
      return res.status(400).json({
        error: 'Email not provided.',
      })
    }
    if (!validator.validate(email)) {
      return res.status(400).json({
        error: 'Invalid email.',
      })
    }
    if (!password) {
      return res.status(400).json({
        error: 'Password not provided.',
      })
    }
    if (!(await login(email, password))) {
      return res.status(401).json({
        error: 'You have invalid credentials',
      })
    }

    console.log(1, email)
    let userId = await getUserId(email)
    console.log(2, userId)
    req.session.user = {
      email,
      id: userId,
    }

    console.log(req.session.user)

    return res.status(200).json({
      message: 'You have successfully logged in.',
      user: req.session.user,
    })
  } catch (err: any) {
    if (err.message === 'This email does not exist.') {
      return res.status(404).json({
        error: err.message,
      })
    } else {
      console.error(err)
      return res.status(500).json({
        error: err
      })
    }
  }
}
