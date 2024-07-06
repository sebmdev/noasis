import CryptoJS from 'crypto-js'
import { Request, Response } from 'express'
// import createPassword from '../util/queries/password/CreatePassword'

export default async function CreatePassword(req: Request, res: Response) {
  try {
    const url = req.body.url
    const username = req.body.username
    const password = req.body.password
    const description = req.body.description || ''

    if (!url) {
      return res.status(400).json({
        error: 'URL not provided',
      })
    }
    if (!username) {
      return res.status(400).json({
        error: 'Username not provided.',
      })
    }
    if (!password) {
      return res.status(400).json({
        error: 'Password not provided.',
      })
    }

    const encrypedPassword = CryptoJS.AES.encrypt(
      password,
      process.env.AES_SECRET_KEY as string
    ).toString()
    console.log(encrypedPassword)
    if (!req.session.user) {
      return res.sendStatus(401)
    }
    // await createPassword(
    //   req.session.user.id as string,
    //   url,
    //   username,
    //   encrypedPassword,
    //   description
    // )

    return res.status(200).json({
      message: 'Created new password',
    })
  } catch (err: any) {
    console.error(err)
    return res.sendStatus(500)
  }
}
