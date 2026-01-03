export interface User {
  userId?: string;
  username: string;
  password?: string;
  name: string;
  surname: string;
  age: number;
  height: number;
  weight: number;
  gender: string;
  lifestyle: string;
  dataSourceId?: string;
  externalSourceUserId?: string;
  diseases: string[];
}
