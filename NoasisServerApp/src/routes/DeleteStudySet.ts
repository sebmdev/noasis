import { Request, Response } from 'express'
import deleteStudySet from './queries/StudySets/DeleteStudySet';

export default async function DeleteStudySet(req: Request, res: Response) {
  try {
    if (!req.session.user) {
      return res.sendStatus(401)
    }

    const studySetId = req.params.id;
    if (!studySetId) {
      return res.status(400).json({
        error: 'Study set not provided.',
      })
    }

    let result = await deleteStudySet(studySetId)
    console.log(result)
    return res.sendStatus(200)

  } catch (err: any) {
    console.error(err)
    return res.sendStatus(500)
  }
}
