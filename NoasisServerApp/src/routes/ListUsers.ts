import { Request, Response } from 'express'
import listUsers from './queries/ListUsers';

export default async function ListUsers(req: Request, res: Response) {
  try {
    if (!req.session.user) {
      return res.sendStatus(401)
    }

    const query = req.query['q'];
    if (!query || query.length == 0 || typeof query !== 'string') {
      return res.status(400).json({
        error: 'Query not provided or invalid.',
      })
    }

    let users = await listUsers(query)
    console.log(users)
    return res.status(200).json(users)
    
  } catch (err: any) {
    console.error(err)
    return res.sendStatus(500)
  }
}
