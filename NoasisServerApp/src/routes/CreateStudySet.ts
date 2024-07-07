import CryptoJS from 'crypto-js'
import { Request, Response } from 'express'
import createStudySet from './queries/StudySets/CreateStudySet'
// import createPassword from '../util/queries/password/CreatePassword'

export default async function CreateStudySet(req: Request, res: Response) {
  try {
    const title = req.body.title || ''

    if (!req.session.user) {
      return res.sendStatus(401)
    }

    await createStudySet(
      req.session.user.id as string,
      title
    )

    return res.status(200).json({
      message: 'Created new study set',
    })
    
  } catch (err: any) {
    console.error(err)
    return res.sendStatus(500)
  }
}
