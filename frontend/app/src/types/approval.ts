export interface ApprovalNode {
  id: string
  nodeName: string
  approverName: string
  status: 'pending' | 'approved' | 'rejected' | 'supplement'
  comment: string
  actionTime: string
}

export interface ApprovalRecord {
  id: string
  type: string
  title: string
  applicantName: string
  status: number
  statusLabel: string
  createdAt: string
  currentStep: string
  timeline: ApprovalNode[]
}

export interface FundApplication {
  id: string
  clubId: string
  clubName: string
  title: string
  amount: number
  status: number
  statusLabel: string
  createdAt: string
}

export interface AssetBorrowRecord {
  id: string
  assetName: string
  borrowerName: string
  clubName: string
  amount: number
  borrowTime: string
  returnTime: string
  status: number
  statusLabel: string
}
