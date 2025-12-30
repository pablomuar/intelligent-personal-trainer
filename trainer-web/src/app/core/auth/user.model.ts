export interface User {
  userId: string;
  username: string;
  age: number;
  height: number;
  weight: number;
  gender: 'MALE' | 'FEMALE' | 'OTHER';
  diseases: string[];
}
