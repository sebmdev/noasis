import { Request, Response } from 'express'
import listStudySets from './queries/StudySets/ListStudySets'

export default async function ListStudySets(req: Request, res: Response) {
  try {

    if (!req.session.user) {
      return res.sendStatus(401)
    }

    let studySets = await listStudySets(req.session.user.id)
    console.log(studySets)
    return res.status(200).json(studySets)
    
  } catch (err: any) {
    console.error(err)
    return res.sendStatus(500)
  }
}
