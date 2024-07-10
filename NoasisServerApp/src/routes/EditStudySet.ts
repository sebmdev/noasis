import { Request, Response } from 'express'
import editStudySet from './queries/StudySets/EditStudySet';

export default async function EditStudySet(req: Request, res: Response) {
  try {
    if (!req.session.user) {
      return res.sendStatus(401)
    }

    const title = req.body.title

    const studySetId = req.params.id;
    if (!studySetId) {
      return res.status(400).json({
        error: 'Study set not provided.',
      })
    }

    let result = await editStudySet(studySetId, title)
    console.log(result)
    return res.sendStatus(200)
    
  } catch (err: any) {
    console.error(err)
    return res.sendStatus(500)
  }
}
